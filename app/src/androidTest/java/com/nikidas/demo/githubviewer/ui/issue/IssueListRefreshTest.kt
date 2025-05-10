import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import com.nikidas.demo.githubviewer.R
import com.nikidas.demo.githubviewer.ui.issue.IssueListActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IssueListRefreshTest {

    @get:Rule
    val activityRule = ActivityTestRule(IssueListActivity::class.java)

    @Test
    fun testSwipeRefresh() {
        onView(withId(R.id.swipeRefreshLayout)).perform(swipeDown())
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }
} 