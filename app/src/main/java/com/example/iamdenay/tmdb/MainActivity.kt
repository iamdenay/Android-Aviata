package com.example.iamdenay.tmdb

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.content.DialogInterface
import android.support.v7.app.AlertDialog


class MainActivity : AppCompatActivity() {

    internal var genre : String? = null
    internal var movieId : String? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        lateinit var fragment : Fragment

        when (item.itemId) {

            R.id.nav_movies -> {
                fragment = MoviesContainer()
            }
            R.id.nav_genres -> {
                fragment = GenresContainer()
            }

        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit()
        return@OnNavigationItemSelectedListener true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val searchViewItem = menu.findItem(R.id.search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = searchViewItem.actionView as SearchView
        searchView.queryHint = "Search for Movies..."
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(false)// Do not iconify the widget; expand it by defaul

        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                // This is your adapter that will be filtered
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                val bundle = Bundle()
                bundle.putString("message", "$query")
                val searchFrag = SearchListView()
                searchFrag.arguments = bundle
                supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, searchFrag)
                        .commit()
                return true
            }
        }
        searchView.setOnQueryTextListener(queryTextListener)

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
                        .add(R.id.frame_layout, MoviesContainer())
                        .commit()

        handleIntent(intent)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onNewIntent(intent: Intent) {

        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            //use the query to search your data somehow
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this@MainActivity)

        // Set the alert dialog title
        builder.setTitle("Leave app?")

        // Display a message on alert dialog
        builder.setMessage("Do you want to leave?")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("YES") { dialog, which ->
            finish()
        }

        // Display a negative button on alert dialog
        builder.setNegativeButton("No") { dialog, which ->
        }
        val dialog: AlertDialog = builder.create()

        dialog.show()

    }
}
