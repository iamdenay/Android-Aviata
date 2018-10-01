package com.example.iamdenay.tmdb

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.iamdenay.tmdb.models.Movie

class PagerAdapter(fragmentManager: FragmentManager) :
        FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> {
                return PopularMovieListFragment()
            }
            1 -> {
                return UpcomingMovieListFragment()
            }
        }
        return PopularMovieListFragment()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Popular"
            1 -> "Upcoming"
            else -> return "Error"
        }
    }

    override fun getCount(): Int {
        return 2
    }
}