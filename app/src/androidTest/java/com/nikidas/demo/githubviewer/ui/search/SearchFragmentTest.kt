import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.nikidas.demo.githubviewer.MainActivity
import com.nikidas.demo.githubviewer.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchFragmentTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testSearchAndOpenDetail() {
        // 先切换到 Search tab
        onView(withId(R.id.navigation_search)).perform(click())
        Thread.sleep(15000)
        // 输入关键字
        onView(withHint("Search Repository")).perform(typeText("android"), ViewActions.closeSoftKeyboard())
        // 等待数据加载（可用IdlingResource优化）
        Thread.sleep(5000)
        // 点击第一个仓库条目
        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(0, click())
        )
        // 检查详情页是否显示
        onView(withText("Repository Details")).check(matches(isDisplayed()))
    }
} 