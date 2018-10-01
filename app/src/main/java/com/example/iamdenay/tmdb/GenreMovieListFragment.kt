package com.example.iamdenay.tmdb

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.iamdenay.tmdb.models.Movie
import kotlinx.android.synthetic.main.fragment_genre_movie_list.*
import kotlinx.android.synthetic.main.fragment_popular_movie_list.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GenreMovieListFragment : Fragment() {

    var movies: ArrayList<Movie> = arrayListOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_genre_movie_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Downloader().execute("https://api.themoviedb.org/3/movie/popular?api_key=1f6c189d328933bfd0c9b198db3f97a8")
        val url = "https://api.themoviedb.org/3/discover/movie?api_key=1f6c189d328933bfd0c9b198db3f97a8" +
                "&language=en-Us" +
                "&sort_by=popularity.desc" +
                "&include_adult=false" +
                "&include_video=false" +
                "&with_genres=${(activity as MainActivity).genre}"

        var progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleSmall)
        val params = RelativeLayout.LayoutParams(30, 30)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        genre_root_frame.addView(progressBar)
        progressBar.visibility = View.VISIBLE
        AsyncTaskHandleJson(object : OnEventListener<String> {
            override fun onSuccess(obj: String) {
                progressBar.visibility = View.GONE
                handleJSON(obj)
            }

            override fun onFailure(e: Exception) {
                progressBar.visibility = View.GONE
                Log.e("Koten", e.message)
            }

        }).execute(url)

    }


    private fun handleJSON(result: String?) {
        val results = JSONObject(result).getJSONArray("results")
        for (i in 0 until results!!.length()) {
            val movie = Movie(results.getJSONObject(i).getInt("id"),
                    results.getJSONObject(i).getString("title"),
                    results.getJSONObject(i).getString("original_title"),
                    results.getJSONObject(i).getDouble("vote_average").toFloat(),
                    results.getJSONObject(i).getInt("vote_count"),
                    results.getJSONObject(i).getString("poster_path"),
                    results.getJSONObject(i).getString("overview"),
                    null,
                    results.getJSONObject(i).getString("release_date"),
                    null,
                    null,
                    null,
                    null
            )
            val tmpView = layoutInflater.inflate(R.layout.movie_item, null)
            tmpView.findViewById<TextView>(R.id.titleTV).text = movie.title
            tmpView.findViewById<TextView>(R.id.overviewTV).text = movie.overview
            tmpView.findViewById<TextView>(R.id.scoreTV).text = "" + movie.score + " of " + movie.votes + " votes"
            val url = "https://image.tmdb.org/t/p/w500" + movie.posterPath
            ImageDownloader(tmpView.findViewById(R.id.imageView)).execute(url);
            tmpView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    (activity as MainActivity).movieId = movie.id.toString()

                    (activity as MainActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, MovieDetailView())
                            .commit()
                }
            })
            genreMovieListLayout.addView(tmpView)
            movies.add(movie)
        }



    }
}
