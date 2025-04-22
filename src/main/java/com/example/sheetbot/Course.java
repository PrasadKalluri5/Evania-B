package com.example.sheetbot;

public class Course {
    private String name;
    private String site;
    private String link;

    public Course(String name, String site, String link) {
        this.name = name;
        this.site = site;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getLink() {
        return link;
    }

    // Optional: helpful for logging or debugging
    @Override
    public String toString() {
        return "Course{name='" + name + "', site='" + site + "', link='" + link + "'}";
    }
}
