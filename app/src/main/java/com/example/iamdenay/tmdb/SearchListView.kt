package com.example.iamdenay.tmdb


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.iamdenay.tmdb.models.Genre
import com.example.iamdenay.tmdb.models.Movie
import kotlinx.android.synthetic.main.fragment_genre_container.view.*
import kotlinx.android.synthetic.main.fragment_popular_movie_list.*
import org.json.JSONObject
import java.net.URL
import android.widget.AdapterView.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_genre_container.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SearchListView : Fragment() {

    private lateinit var listView: ListView
    private var movies: ArrayList<Movie> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_genre_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity
        val strtext = arguments!!.getString("message")
        val url = "https://api.themoviedb.org/3/search/movie?api_key=1f6c189d328933bfd0c9b198db3f97a8" +
                "&query=${strtext}"
        var progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleSmall)
        val params = RelativeLayout.LayoutParams(30, 30)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        genres_root.addView(progressBar)
        progressBar.setVisibility(View.VISIBLE)

        AsyncTaskHandleJson(object : OnEventListener<String> {
            override fun onSuccess(obj: String) {
                progressBar.setVisibility(View.GONE)
                val listItems = handleJSON(obj)
                listView = view.genres_list_view

                listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
                    activity.movieId = movies.get(position).id.toString()

                    (activity as MainActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, MovieDetailView())
                            .commit()
                }

                val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, listItems)
                listView.adapter = adapter

            }

            override fun onFailure(e: Exception) {
                progressBar.setVisibility(View.GONE)
                Log.e("Error", e.message)
            }


        }).execute(url)

    }

    private fun handleJSON(result: String?) : Array<String?> {
        val results = JSONObject(result).getJSONArray("results")
        val listItems = arrayOfNulls<String>(results.length())
        for (i in 0..results!!.length() - 1) {
            val movie = Movie(results.getJSONObject(i).getInt("id"),
                    results.getJSONObject(i).getString("title")
            )
            listItems[i] = movie.title
            movies.add(movie)
        }
        return listItems


    }
}
