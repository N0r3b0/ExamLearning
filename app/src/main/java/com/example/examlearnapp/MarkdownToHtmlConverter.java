package com.example.examlearnapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownToHtmlConverter {

    public static String markdownToHtml(String markdown) {
        // Nagłówki
        markdown = markdown
                .replaceAll("######\\s+(.*?)\\n", "<h6>$1</h6>")
                .replaceAll("#####\\s+(.*?)\\n", "<h5>$1</h5>")
                .replaceAll("####\\s+(.*?)\\n", "<h4>$1</h4>")
                .replaceAll("###\\s+(.*?)\\n", "<h3>$1</h3>")
                .replaceAll("##\\s+(.*?)\\n", "<h2>$1</h2>")
                .replaceAll("#\\s+(.*?)\\n", "<h1>$1</h1>");

        // Pogrubienie i kursywa
        markdown = markdown
                .replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>")
                .replaceAll("\\*(.*?)\\*", "<em>$1</em>")
                .replaceAll("__(.*?)__", "<strong>$1</strong>")
                .replaceAll("_(.*?)_", "<em>$1</em>");

        // Linki
        markdown = markdown.replaceAll("\\[(.*?)\\]\\((.*?)\\)", "<a href=\"$2\">$1</a>");

        // Obrazy
        markdown = markdown.replaceAll("!\\[(.*?)\\]\\((.*?)\\)", "<img src=\"$2\" alt=\"$1\">");

        // Listy nienumerowane
        markdown = convertUnorderedLists(markdown);

        // Listy numerowane
        markdown = convertOrderedLists(markdown);

        // Bloki kodu
        markdown = markdown.replaceAll("```(.*?)\\n(.*?)\\n```", "<pre><code>$2</code></pre>");

        // Cytaty
        markdown = markdown.replaceAll(">\\s+(.*?)\\n", "<blockquote>$1</blockquote>");

        // Linie poziome
        markdown = markdown.replaceAll("\\n-{3,}\\n", "<hr>");

        // Nowe linie
        markdown = markdown.replaceAll("\\n", "<br>");

        return markdown;
    }

    private static String convertUnorderedLists(String markdown) {
        StringBuilder html = new StringBuilder();
        String[] lines = markdown.split("\\n");
        boolean inList = false;

        for (String line : lines) {
            if (line.matches("\\s*[-*+]\\s+.*")) {
                if (!inList) {
                    html.append("<ul>");
                    inList = true;
                }
                html.append("<li>").append(line.replaceFirst("\\s*[-*+]\\s+", "")).append("</li>");
            } else {
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }
                html.append(line).append("<br>");
            }
        }

        if (inList) {
            html.append("</ul>");
        }

        return html.toString();
    }

    private static String convertOrderedLists(String markdown) {
        StringBuilder html = new StringBuilder();
        String[] lines = markdown.split("\\n");
        boolean inList = false;

        for (String line : lines) {
            if (line.matches("\\s*\\d+\\.\\s+.*")) {
                if (!inList) {
                    html.append("<ol>");
                    inList = true;
                }
                html.append("<li>").append(line.replaceFirst("\\s*\\d+\\.\\s+", "")).append("</li>");
            } else {
                if (inList) {
                    html.append("</ol>");
                    inList = false;
                }
                html.append(line).append("<br>");
            }
        }

        if (inList) {
            html.append("</ol>");
        }

        return html.toString();
    }

//    public static void main(String[] args) {
//        String markdown = "# Nagłówek 1\n" +
//                "## Nagłówek 2\n" +
//                "### Nagłówek 3\n" +
//                "#### Nagłówek 4\n" +
//                "##### Nagłówek 5\n" +
//                "###### Nagłówek 6\n" +
//                "**Pogrubienie** i *kursywa*\n" +
//                "[Link](https://example.com)\n" +
//                "![Obraz](https://example.com/image.png)\n" +
//                "- Element listy 1\n" +
//                "- Element listy 2\n" +
//                "1. Element listy numerowanej 1\n" +
//                "2. Element listy numerowanej 2\n" +
//                "```\n" +
//                "Blok kodu\n" +
//                "```\n" +
//                "> Cytat\n" +
//                "---\n";
//
//        System.out.println(markdownToHtml(markdown));
//    }
}