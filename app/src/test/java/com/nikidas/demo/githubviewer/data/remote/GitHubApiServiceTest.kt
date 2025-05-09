import com.nikidas.demo.githubviewer.data.remote.GitHubApiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*

class GitHubApiServiceTest {

    @Test
    fun testSearchRepositoriesSync_success() {
        val api = GitHubApiService()
        val result = api.searchRepositoriesSync("android")
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
        assertTrue(result[0].name.isNotEmpty())
    }

    @Test
    fun testGetRepository_notFound() {
        val api = GitHubApiService()
        try {
            api.getRepository("not_exist_owner", "not_exist_repo")
            fail("Should throw exception for not found repo")
        } catch (e: Exception) {
            assertTrue(e.message?.contains("失败") == true)
        }
    }

    @Test
    fun testGetRepoIssues_success() = runBlocking {
        val api = GitHubApiService()
        val issues = api.getRepoIssues("square", "retrofit")
        assertNotNull(issues)
        assertTrue(issues.isNotEmpty())
    }
} 