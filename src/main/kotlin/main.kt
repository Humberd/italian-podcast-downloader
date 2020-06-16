import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import java.io.File
import java.net.URL

class Main

fun main() {
    println("Hello world")
    val episodes = Gson().fromJson<List<Episode>>(getFileFromResources("episodes-definition.json").reader())
    println(episodes)
}

fun getFileFromResources(fileName: String): File {
    val classLoader = Main().javaClass.classLoader
    val resource: URL? = classLoader.getResource(fileName)
    return if (resource == null) {
        throw IllegalArgumentException("file is not found!")
    } else {
        File(resource.getFile())
    }
}