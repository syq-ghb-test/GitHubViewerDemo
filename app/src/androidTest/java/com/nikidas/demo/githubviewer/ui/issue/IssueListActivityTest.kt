import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import com.nikidas.demo.githubviewer.ui.issue.IssueListActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IssueListActivityTest {

    @get:Rule
    val activityRule = ActivityTestRule(IssueListActivity::class.java)

    @Test
    fun testCreateIssue() {
        // 点击添加Issue按钮
        onView(withId(R.id.fabAddIssue)).perform(click())
        // 输入标题
        onView(withId(R.id.etTitle)).perform(typeText("UI自动化测试Issue"), closeSoftKeyboard())
        // 输入正文
        onView(withId(R.id.etBody)).perform(typeText("自动化测试正文"), closeSoftKeyboard())
        // 点击发送
        onView(withId(R.id.fabSend)).perform(click())
        // 检查是否返回到Issue列表页
        Thread.sleep(2000)
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }
} 