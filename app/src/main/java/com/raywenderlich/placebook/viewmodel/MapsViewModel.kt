package com.raywenderlich.placebook.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.raywenderlich.placebook.model.Bookmark
import com.raywenderlich.placebook.repository.BookmarkRepo

class MapsViewModel(application: Application) :
    AndroidViewModel(application) {
    private var bookmarks: LiveData<List<BookmarkMarkerView>>? = null
    private val TAG = "MapsViewModel"
    // 2
    private val bookmarkRepo: BookmarkRepo = BookmarkRepo(
        getApplication())
    // takes in a Google Place and a Bitmap image.
    fun addBookmarkFromPlace(place: Place, image: Bitmap?) {
        // 4
        val bookmark = bookmarkRepo.createBookmark()
        bookmark.placeId = place.id
        bookmark.name = place.name.toString()
        bookmark.longitude = place.latLng?.longitude ?: 0.0
        bookmark.latitude = place.latLng?.latitude ?: 0.0
        bookmark.phone = place.phoneNumber.toString()
        bookmark.address = place.address.toString()
        // 5
        val newId = bookmarkRepo.addBookmark(bookmark)
        Log.i(TAG, "New bookmark $newId added to the database.")
    }
    data class BookmarkMarkerView(
        var id: Long? = null,
        var location: LatLng = LatLng(0.0, 0.0))
    private fun bookmarkToMarkerView(bookmark: Bookmark) =
        BookmarkMarkerView(
            bookmark.id,
            LatLng(bookmark.latitude, bookmark.longitude))

    private fun mapBookmarksToMarkerView() {
        // 1
        bookmarks = Transformations.map(bookmarkRepo.allBookmarks)
        { repoBookmarks ->
            // 2
            repoBookmarks.map { bookmark ->
                bookmarkToMarkerView(bookmark)
            }
        }}
    fun getBookmarkMarkerViews() :
            LiveData<List<BookmarkMarkerView>>? {
        if (bookmarks == null) {
            mapBookmarksToMarkerView()
        }
        return bookmarks
    }
    }