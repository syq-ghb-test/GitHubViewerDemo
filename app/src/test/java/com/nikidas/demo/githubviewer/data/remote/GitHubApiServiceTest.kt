import android.content.SharedPreferences
import com.nikidas.demo.githubviewer.MyApplication
import com.nikidas.demo.githubviewer.data.model.Owner
import com.nikidas.demo.githubviewer.data.model.Repository
import com.nikidas.demo.githubviewer.data.remote.GitHubApiService
import com.nikidas.demo.githubviewer.data.remote.Issue
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.anyOrNull

class GitHubApiServiceTest {

    @Test
    fun testSearchRepositoriesSync_success() {
        val api = mock(GitHubApiService::class.java)
        val fakeList = listOf(
            Repository(
                id = 1L,
                name = "repo",
                description = "desc",
                stargazers_count = 100,
                language = "Kotlin",
                owner = Owner("testuser", "http://avatar.url")
            )
        )
        `when`(api.searchRepositoriesSync("android")).thenReturn(fakeList)
        val result = api.searchRepositoriesSync("android")
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
        assertTrue(result[0].name.isNotEmpty())
    }

    @Test
    fun testGetRepository_notFound() {
        val api = mock(GitHubApiService::class.java)
        doThrow(RuntimeException("获取仓库详情失败: 404, body=not found"))
            .`when`(api).getRepository("not_exist_owner", "not_exist_repo")
        try {
            api.getRepository("not_exist_owner", "not_exist_repo")
            fail("Should throw exception for not found repo")
        } catch (e: Exception) {
            assertTrue(e.message?.contains("失败") == true)
        }
    }

    @Test
    fun testGetRepoIssues_success() = runBlocking {
        val api = mock(GitHubApiService::class.java)
        val fakeIssues = listOf(
            Issue(1, 1, "title1", "2024-05-10T15:00:00Z", 0),
            Issue(2, 2, "title2", "2024-05-11T15:00:00Z", 1)
        )
        `when`(api.getRepoIssues("square", "retrofit")).thenReturn(fakeIssues)
        val issues = api.getRepoIssues("square", "retrofit")
        assertNotNull(issues)
        assertTrue(issues.isNotEmpty())
    }
}