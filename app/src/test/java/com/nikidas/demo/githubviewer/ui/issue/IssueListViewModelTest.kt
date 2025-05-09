import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nikidas.demo.githubviewer.data.remote.GitHubApiService
import com.nikidas.demo.githubviewer.data.remote.Issue
import com.nikidas.demo.githubviewer.ui.issue.IssueListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class IssueListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val api = mock(GitHubApiService::class.java)
    private lateinit var viewModel: IssueListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = IssueListViewModel(mock(android.app.Application::class.java))
    }

    @Test
    fun testLoadIssues_success() = runTest {
        val fakeIssues = listOf(
            Issue(1, 1, "title1", "2024-05-10T15:00:00Z", 0),
            Issue(2, 2, "title2", "2024-05-11T15:00:00Z", 1)
        )
        `when`(api.getRepoIssues("owner", "repo")).thenReturn(fakeIssues)
        viewModel.loadIssues("owner", "repo")
        Assert.assertEquals(fakeIssues, viewModel.issues.value)
    }

    @Test
    fun testLoadIssues_error() = runTest {
        `when`(api.getRepoIssues("owner", "repo")).thenThrow(RuntimeException("error"))
        viewModel.loadIssues("owner", "repo")
        Assert.assertEquals("error", viewModel.error.value)
    }
} 