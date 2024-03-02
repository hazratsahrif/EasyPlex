package com.siflusso.ui.moviedetails.model;

public class OverviewModel {
    String overView, genre;
    public OverviewModel(String overView, String genre) {
        this.overView = overView;
        this.genre = genre;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
