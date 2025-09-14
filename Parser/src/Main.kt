fun main() {
    println("=== 🕷️ Упрощённый HTML-парсер ===")

    // Симулируем загрузку HTML-страницы
    val html = """
        <html>
          <head>
            <title>GitHub.sultanov.nisredin911@gmail.com</title>
          </head>
          <body>
            <h1>Добро пожаловать!</h1>
            <p>Это мой <a href="https://Tuc.com">сайт</a>.</p>
            <p>Смотрите фото: <img src="cat.jpg" alt="Кот"></p>
            <a href="https://sult.com">Магазин</a>
            <h2>Контакты</h2>
          </body>
        </html>
    """.trimIndent()

    val parser = SimpleHtmlParser(html)

    println("\n🔍 Найдено:")
    parser.titles.forEach { println("  📄 Заголовок страницы: $it") }
    parser.headings.forEach { (level, text) ->
        println("  📚 H$level: $text")
    }
    parser.links.forEach { (text, url) ->
        println("  🔗 Ссылка [$text]: $url")
    }
    parser.images.forEach { (src, alt) ->
        println("  🖼 Изображение: $src (подпись: '$alt')")
    }

    println("\n✅ Парсинг завершён!")
}

// Класс для парсинга HTML
class SimpleHtmlParser(private val html: String) {

    val titles = extractAll("<title>", "</title>")
    val headings = mutableListOf<Pair<Int, String>>() // (уровень, текст)
    val links = mutableListOf<Pair<String, String>>()  // (текст, href)
    val images = mutableListOf<Pair<String, String>>() // (src, alt)

    init {
        // Извлекаем h1, h2, h3
        for (level in 1..3) {
            val tag = "h$level"
            extractAll("<$tag>", "</$tag>").forEach {
                headings.add(level to it)
            }
        }

        // Извлекаем ссылки: <a href="url">текст</a>
        var i = 0
        while (i < html.length) {
            val linkStart = html.indexOf("<a ", i)
            if (linkStart == -1) break

            val hrefStart = html.indexOf("href=\"", linkStart)
            if (hrefStart == -1) break
            val hrefEnd = html.indexOf("\"", hrefStart + 6)
            if (hrefEnd == -1) break
            val url = html.substring(hrefStart + 6, hrefEnd)

            val openTagEnd = html.indexOf(">", hrefEnd)
            if (openTagEnd == -1) break
            val closeTag = "</a>"
            val closeTagPos = html.indexOf(closeTag, openTagEnd)
            if (closeTagPos == -1) break
            val text = html.substring(openTagEnd + 1, closeTagPos)

            links.add(text.trim() to url)
            i = closeTagPos + closeTag.length
        }

        // Извлекаем изображения: <img src="..." alt="...">
        i = 0
        while (i < html.length) {
            val imgStart = html.indexOf("<img ", i)
            if (imgStart == -1) break

            val srcStart = html.indexOf("src=\"", imgStart)
            val altStart = html.indexOf("alt=\"", imgStart)

            var src = "неизвестно"
            var alt = "без подписи"

            if (srcStart != -1) {
                val srcEnd = html.indexOf("\"", srcStart + 5)
                if (srcEnd != -1) {
                    src = html.substring(srcStart + 5, srcEnd)
                }
            }

            if (altStart != -1) {
                val altEnd = html.indexOf("\"", altStart + 5)
                if (altEnd != -1) {
                    alt = html.substring(altStart + 5, altEnd)
                }
            }

            images.add(src to alt)
            i = imgStart + 5
        }
    }

    // Универсальный метод: извлекает всё между тегами
    private fun extractAll(openTag: String, closeTag: String): List<String> {
        val result = mutableListOf<String>()
        var i = 0
        while (i < html.length) {
            val start = html.indexOf(openTag, i)
            if (start == -1) break
            val end = html.indexOf(closeTag, start + openTag.length)
            if (end == -1) break
            val content = html.substring(start + openTag.length, end)
            result.add(content.trim())
            i = end + closeTag.length
        }
        return result
    }
}