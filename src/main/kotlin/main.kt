import com.github.salomonbrys.kotson.fromJson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.InputStream
import java.text.CharacterIterator
import java.text.StringCharacterIterator


suspend fun main() {
    val gson = GsonBuilder().setPrettyPrinting().create()
    val client = OkHttpClient()

    val episodesFile = File("episodes/definition.json")

    val episodes = gson.fromJson<List<Episode>>(episodesFile.reader())
    println("Total episodes: ${episodes.size}")
    val episodesNotYetDownloaded = episodes.filter { !it.isDownloaded }
    println("Downloaded: ${episodes.size - episodesNotYetDownloaded.size}")
    println("Not downloaded: ${episodesNotYetDownloaded.size}")


//    val twoThreadsDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher();

    coroutineScope {
        episodesNotYetDownloaded.forEach {
            launch {
                downloadEpisode(client, it)
                it.isDownloaded = true
                episodesFile.writeText(gson.toJson(episodes))
            }
        }
    }
}

fun downloadEpisode(client: OkHttpClient, episode: Episode) {
    println("Episode ${episode.episodeNumber} -> Downloading...")
    val request = Request.Builder()
        .url("https://www.newsinslowitalian.com/intermediate-italian/pdf/${episode.episodeNumber}/episode.pdf")
        .build()

    val response = client.newCall(request).execute()
    val body = response.body

    println("Episode ${episode.episodeNumber} -> Downloading Success! ${humanReadableByteCountSI(body!!.contentLength())}")
    println("Episode ${episode.episodeNumber} -> Saving pdf...")
    createMp3File(episode, body.byteStream())
    println("Episode ${episode.episodeNumber} -> Saving pdf - Success!")
}

fun createMp3File(episode: Episode, byteStream: InputStream) {
    val file = File("episodes/files/${episode.episodeNumber}-${episode.year}.pdf")
    file.createNewFile()

    file.writeBytes(byteStream.readBytes())
}

fun humanReadableByteCountSI(bytesInitial: Long): String? {
    var bytes = bytesInitial
    if (-1000 < bytes && bytes < 1000) {
        return "$bytes B"
    }
    val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
    while (bytes <= -999950 || bytes >= 999950) {
        bytes /= 1000
        ci.next()
    }
    return String.format("%.1f %cB", bytes / 1000.0, ci.current())
}