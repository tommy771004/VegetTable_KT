package com.vegettable.app

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vegettable.app.network.ApiClient
import com.vegettable.app.ui.favorites.FavoritesFragment
import com.vegettable.app.ui.home.HomeFragment
import com.vegettable.app.ui.search.SearchFragment
import com.vegettable.app.ui.settings.SettingsFragment
import com.vegettable.app.util.PermissionManager

class MainActivity : AppCompatActivity() {

    private lateinit var locationLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var notificationLauncher: ActivityResultLauncher<String>

    private lateinit var homeFragment: HomeFragment
    private lateinit var searchFragment: SearchFragment
    private lateinit var favoritesFragment: FavoritesFragment
    private lateinit var settingsFragment: SettingsFragment
    private lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化 OkHttp 快取
        ApiClient.init(application)

        // 註冊權限請求 Launcher
        locationLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            // 權限請求完成 (無論成功或失敗，應用都能繼續運行)
        }

        notificationLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            // 權限請求完成
        }

        // 請求權限 (如果尚未授予)
        if (!PermissionManager.hasLocationPermission(this)) {
            PermissionManager.requestLocationPermission(this, locationLauncher)
        }

        if (!PermissionManager.hasNotificationPermission(this)) {
            PermissionManager.requestNotificationPermission(this, notificationLauncher)
        }

        if (savedInstanceState == null) {
            homeFragment = HomeFragment()
            searchFragment = SearchFragment()
            favoritesFragment = FavoritesFragment()
            settingsFragment = SettingsFragment()

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, settingsFragment, "settings").hide(settingsFragment)
                .add(R.id.fragment_container, favoritesFragment, "favorites").hide(favoritesFragment)
                .add(R.id.fragment_container, searchFragment, "search").hide(searchFragment)
                .add(R.id.fragment_container, homeFragment, "home")
                .commit()

            activeFragment = homeFragment
        } else {
            homeFragment = supportFragmentManager.findFragmentByTag("home") as HomeFragment
            searchFragment = supportFragmentManager.findFragmentByTag("search") as SearchFragment
            favoritesFragment = supportFragmentManager.findFragmentByTag("favorites") as FavoritesFragment
            settingsFragment = supportFragmentManager.findFragmentByTag("settings") as SettingsFragment
            
            // 找出當前顯示的 Fragment
            activeFragment = listOf(homeFragment, searchFragment, favoritesFragment, settingsFragment)
                .firstOrNull { !it.isHidden } ?: homeFragment
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            val target: Fragment = when (item.itemId) {
                R.id.nav_home -> homeFragment
                R.id.nav_search -> searchFragment
                R.id.nav_favorites -> favoritesFragment
                R.id.nav_settings -> settingsFragment
                else -> return@setOnItemSelectedListener false
            }

            supportFragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(target)
                .commit()
            activeFragment = target
            true
        }
    }
}
