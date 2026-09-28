package delivery_service.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
public class PageController {

    private static final Map<String, String> PAGES = Map.of(
            "index", "index",
            "login", "login",
            "register", "register"
    );

    private static final Map<String, String> TITLES = Map.of(
            "index", "Котобус - доставка по городу",
            "login", "Вход",
            "register", "Регистрация"
    );

    private static final Map<String, String> CSS = Map.of(
            "login", "/css/auth.css",
            "register", "/css/auth.css"
    );

    private String header;
    private String footer;

    @PostConstruct
    void loadFragments() throws IOException {
        header = read("static/html/fragments/header.html");
        footer = read("static/html/fragments/footer.html");
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String home() throws IOException{
        return render("index");
    }

    @GetMapping(value = "/{page:[^\\.]*}", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String page(@PathVariable String page) throws IOException {
        if (!PAGES.containsKey(page)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return render(page);
    }

    private String read(String path) throws IOException {
        try (InputStream in = new ClassPathResource(path).getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String render(String page) throws IOException {
        String body  = read("static/html/" + PAGES.get(page) + ".html");
        String title = TITLES.getOrDefault(page, "Котобус");

        StringBuilder css = new StringBuilder();
        css.append("<link rel=\"stylesheet\" href=\"/css/style.css\">\n");
        if (CSS.containsKey(page)) {
            css.append("<link rel=\"stylesheet\" href=\"")
                    .append(CSS.get(page))
                    .append("\">\n");
        }

        return """
        <!DOCTYPE html>
        <html lang="ru">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>""" + title + """
        </title>
        """ + css + """
        </head>
        <body>
        """ + header + body + footer + """
        </body>
        </html>
        """;
    }
}
