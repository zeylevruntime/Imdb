package com.tes.imdb;

import java.util.List;

public class MovieList {
    List<SearchResult> Search = null;
    String totalResults,Response;

    public List<SearchResult> getSearch() {
        return Search;
    }

    public void setSearch(List<SearchResult> search) {
        this.Search = search;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getResponse() {
        return Response;
    }

    public void setResponse(String response) {
        Response = response;
    }


}
