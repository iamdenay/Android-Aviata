package com.example.iamdenay.tmdb


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.iamdenay.tmdb.models.Actor
import com.example.iamdenay.tmdb.models.Genre
import com.example.iamdenay.tmdb.models.Movie
import kotlinx.android.synthetic.main.fragment_genre_movie_list.*
import kotlinx.android.synthetic.main.fragment_movie_detail_view.*
import kotlinx.android.synthetic.main.slider_item.*
import kotlinx.android.synthetic.main.slider_item.view.*
import org.json.JSONObject
import java.net.URL


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MovieDetailView : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_detail_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = "https://api.themoviedb.org/3/movie/${(activity as MainActivity).movieId}?api_key=1f6c189d328933bfd0c9b198db3f97a8" +
                "&append_to_response=credits,similar"

        val imgUrl = "https://api.themoviedb.org/3/movie/${(activity as MainActivity).movieId}/images" +
                "?api_key=1f6c189d328933bfd0c9b198db3f97a8&include_image_language=en"

        var progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleSmall)
        val params = RelativeLayout.LayoutParams(30, 30)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        detail_root.addView(progressBar)
        progressBar.visibility = View.VISIBLE
        AsyncTaskHandleJson(object : OnEventListener<String> {
            override fun onSuccess(obj: String) {
                progressBar.visibility = View.GONE
                handleImag(obj)
            }

            override fun onFailure(e: Exception) {
                progressBar.visibility = View.GONE
                Log.e("Koten", e.message)
            }

        }).execute(imgUrl)

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
        val results = JSONObject(result)
        val movie = Movie(results.getInt("id"),
                results.getString("title"),
                results.getString("original_title"),
                results.getDouble("vote_average").toFloat(),
                results.getInt("vote_count"),
                results.getString("poster_path"),
                results.getString("overview"),
                results.getInt("budget"),
                results.getString("release_date"),
                null,
                results.getInt("runtime"),
                null,
                null
        )
        val url = "https://image.tmdb.org/t/p/w500" + movie.posterPath
        if (!url.contains("null")) {
            Log.d("posterPAATH", url)

            ImageDownloader(imageVewDetail).execute(url);
        }
        var genres : ArrayList<Genre> = arrayListOf()
        var jsonGenres = results.getJSONArray("genres")
        var str = "" + movie.duration
        for(i in 0 until jsonGenres.length()){
            genres.add(Genre(
                    jsonGenres.getJSONObject(i).getInt("id"),
                    jsonGenres.getJSONObject(i).getString("name")
            ))
            str += " " + genres[i].name
        }
        durationTV.text = str
        var actors : ArrayList<Actor> = arrayListOf()
        var jsonActors = results.getJSONObject("credits").getJSONArray("cast")
        for(i in 0 until jsonActors.length()){
            actors.add(Actor(
                    jsonActors.getJSONObject(i).getString("name"),
                    jsonActors.getJSONObject(i).getString("character"),
                    jsonActors.getJSONObject(i).getString("profile_path")
            ))
            val actorView = layoutInflater.inflate(R.layout.slider_item, null)
            actorView.firstLine.text = actors[i].name
            actorView.secondLine.text = actors[i].char
            val url = "https://image.tmdb.org/t/p/w500" + actors[i].profilePath
            if (!url.contains("null")){
                Log.d("actorPAATH", url)
                ImageDownloader(actorView.firstImage).execute(url);
            }
            actors_layout.addView(actorView)
        }
        var similar : ArrayList<Movie> = arrayListOf()
        var jsonSimilar = results.getJSONObject("similar").getJSONArray("results")
        for(i in 0 until jsonSimilar.length()){
            similar.add(Movie(
                    jsonSimilar.getJSONObject(i).getInt("id"),
                    jsonSimilar.getJSONObject(i).getString("title"),
                    jsonSimilar.getJSONObject(i).getString("original_title"),
                    jsonSimilar.getJSONObject(i).getDouble("vote_average").toFloat(),
                    jsonSimilar.getJSONObject(i).getInt("vote_count"),
                    jsonSimilar.getJSONObject(i).getString("poster_path"),
                    jsonSimilar.getJSONObject(i).getString("overview"),
                    null,
                    jsonSimilar.getJSONObject(i).getString("release_date"),
                    null,
                    null,
                    null,
                    null
            ))
            val similarView = layoutInflater.inflate(R.layout.slider_item, null)
            similarView.firstLine.text = similar[i].title
                val url = "https://image.tmdb.org/t/p/w500" + similar[i].posterPath
            if (!url.contains("null")) {
                Log.d("similarPAATH", url)
                ImageDownloader(similarView.firstImage).execute(url);
            }
            similarView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    (activity as MainActivity).movieId = similar[i].id.toString()

                    (activity as MainActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, MovieDetailView())
                            .commit()
                }
            })
            similar_layout.addView(similarView)
        }
        movie.genres = genres
        movie.actors = actors
        movie.similar = similar
        titleTV.text = movie.title
        overviewTV.text = movie.overview
        budgetTV.text = movie.budget.toString()
        releaseTV.text = movie.releaseDate


    }

    private fun handleImag(result: String?) {
        val results = JSONObject(result)

        var images = results.getJSONArray("backdrops")
        for(i in 0 until images.length()){
            val imgView = layoutInflater.inflate(R.layout.slider_item, null)
            imgView.firstLine.text = ""
            val url = "https://image.tmdb.org/t/p/w500" + images.getJSONObject(i).getString("file_path")
            if (!url.contains("null")){
                ImageDownloader(imgView.firstImage).execute(url);
            }
            images_layout.addView(imgView)
        }


    }

}
