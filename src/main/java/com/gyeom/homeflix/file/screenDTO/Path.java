package com.gyeom.homeflix.file.screenDTO;

public class Path {
    private String urlVariable;
    private String name;

    public Path(String urlVariable, String name){
        this.urlVariable = urlVariable;
        this.name = name;
    }

    public String getUrlVariable() {
        return urlVariable;
    }

    public String getName() {
        return name;
    }
}
