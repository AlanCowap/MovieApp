package com.example.android.app.moviesapp;

import android.util.Log;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Laurence on 21/05/2017.
 *
 */

class Movie {
//TODO AWEESOME You created a class to encapsulate the state and behaviour for Movies.
    //Debug tag name of class
    private static String TAG = Movie.class.getSimpleName();

    private static final String POSTER_PATH_A = "http://image.tmdb.org/t/p";
    private static final String POSTER_SIZE = "w185";
    private static final String URL_PATH_SEPARATOR = "/";
    private static final String URL_NON_PATH_SEPARATOR = "\\";


    private static String mRootPosterUrl;

    static void setRootPosterUrl(){
        mRootPosterUrl = POSTER_PATH_A + URL_PATH_SEPARATOR + POSTER_SIZE;
    }

    //holds the JSON object received poster path
    private String posterPath;

    String getPosterPath(){
        return posterPath;
    }

    void setPosterPath(String path){
        if(path.startsWith(URL_NON_PATH_SEPARATOR)){
            path = path.substring(1);
        }
        path = URL_PATH_SEPARATOR + path;
        path = mRootPosterUrl + path;
        posterPath = path;
    }

    private boolean mAdult;

    public boolean getAdult(){
        return mAdult;
    }

    void setAdult(boolean adult){

        mAdult = adult;
    }

    void setAdult(String adult){
        mAdult = Boolean.parseBoolean(adult);
    }

    private String mOverview;
    String getOverview(){
        return mOverview;
    }
    void setOverview(String overview){
        mOverview = overview;
    }

    private Date mReleaseDate;
    Date getReleaseDate(){
        return mReleaseDate;
    }
    void setReleaseDate(Date date){
        mReleaseDate = date;
    }
    // Takes international standard yyyy-MM-dd format
    //Note if the date cannot be parsed, it will return null
    void setReleaseDate(String date){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try{
            mReleaseDate = format.parse(date);
        }
        catch (ParseException e){
            mReleaseDate = null;
        }
    }

    private ArrayList<Integer> mGenre_ids;
    ArrayList<Integer> getGenre(){
        return mGenre_ids;
    }
    void setGenre(Integer[] genres){
        mGenre_ids = new ArrayList<>(Arrays.asList(genres));
    }
    void setGenre(ArrayList<Integer> genres){
        mGenre_ids = new ArrayList<>();
            mGenre_ids.addAll(genres);
    }
    void setGenre(String[] genres){
        mGenre_ids = new ArrayList<>();
        for(String s : genres){
            mGenre_ids.add(Integer.parseInt(s));
        }
    }

    int getId() {
        return mId;
    }

    void setId(int id) {
        this.mId = id;
    }
    void setId(String id) {
            this.mId = Integer.parseInt(id);
    }

    String getOriginalTitle() {
        return mOriginalTitle;
    }

    void setOriginalTitle(String originalTitle) {
        this.mOriginalTitle = originalTitle;
    }

    String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    void setOriginalLanguage(String originalLanguage) {
        this.mOriginalLanguage = originalLanguage;
    }

    // The local language title
    String getTitle() {
        return mTitle;
    }

    void setTitle(String title) {
        this.mTitle = title;
    }

    String getBackDropPath() {
        return mBackDropPath;
    }

    void setBackDropPath(String path) {
        if(path.startsWith(URL_PATH_SEPARATOR)){
            path = path.substring(1);
        }
        this.mBackDropPath = path;
    }

    Double getPopularity() {
        return mPopularity;
    }

    void setPopularity(Double popularity) {
        this.mPopularity = popularity;
    }
    void setPopularity(String popularity) {
        this.mPopularity = Double.parseDouble(popularity);
    }

    long getVoteCount() {
        return mVoteCount;
    }

    void setVoteCount(long voteCount) {
        this.mVoteCount = voteCount;
    }
    void setVoteCount(String voteCount) {
        this.mVoteCount = Long.parseLong(voteCount);
    }

    boolean isVideo() {
        return mVideo;
    }

    void setVideo(boolean video) {
        this.mVideo = video;
    }
    void setVideo(String video) {
        this.mVideo = Boolean.parseBoolean(video);
    }

    Double getVote_Average() {
        return mVote_Average;
    }

    void setVote_Average(Double vote_Average) {
        this.mVote_Average = vote_Average;
    }

    void setVote_Average(String vote_Average) {
        Log.d(TAG, "vote_Avg: " + vote_Average);
        this.mVote_Average =Double.parseDouble(vote_Average);
    }

    private int mId;

    private String mOriginalTitle;

    private String mOriginalLanguage;

    private String mTitle;

    private String mBackDropPath;

    private Double mPopularity;

    private long mVoteCount;

    private boolean mVideo;

    private Double mVote_Average;

    private int mPageNumber;

    void setPageNumber(int pageNo){
        mPageNumber = pageNo;
    }
    void setPageNumber(String pageNo){
        mPageNumber = Integer.parseInt(pageNo);
    }
    int getPageNumber(){
        return mPageNumber;
    }
}
