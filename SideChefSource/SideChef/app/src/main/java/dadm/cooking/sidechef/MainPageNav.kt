package dadm.cooking.sidechef

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import dadm.cooking.sidechef.databinding.ActivityMainPageNavBinding

class MainPageNav : AppCompatActivity() {

    private lateinit var binding: ActivityMainPageNavBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainPageNavBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        replaceContent(Home())
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homePage -> replaceContent(Home())
                R.id.searchPage -> replaceContent(Search())
                R.id.profilePage -> replaceContent(Profile())
            }
            true
        }
    }

    private fun replaceContent(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.ContentframeLayout, fragment)
        fragmentTransaction.commit()
    }

}