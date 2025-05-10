import com.nikidas.demo.githubviewer.data.model.Owner
import com.nikidas.demo.githubviewer.data.model.Repository
import com.nikidas.demo.githubviewer.data.remote.GitHubApiService
import com.nikidas.demo.githubviewer.data.repository.SearchRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*

class SearchRepositoryTest {

    @Test
    fun testSearchRepositories_success() = runBlocking {
        val api = mock(GitHubApiService::class.java)
        val repo = SearchRepository(api)
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
        val result = repo.searchRepositories("android")
        assertEquals(fakeList, result)
    }
} 