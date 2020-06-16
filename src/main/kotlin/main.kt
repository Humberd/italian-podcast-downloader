import com.github.salomonbrys.kotson.fromJson
import com.google.gson.GsonBuilder
import java.io.File

class Main

fun main() {
    val gson = GsonBuilder().setPrettyPrinting().create()
    val episodesFile = File("episodes-definition.json")
    val episodes = gson.fromJson<List<Episode>>(episodesFile.reader())
    println(episodes)
    episodesFile.writeText(gson.toJson(episodes))
}
