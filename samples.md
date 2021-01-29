### Reference example
Android development is very strict on the blocking the main UI thread.
And it order to make a call using our library you might want to wrap the call into AsyncTask or simillar classes which allows executing async operations on a separate threads
This example is in Kotlin:
```kotlin
import android.os.AsyncTask
import com.unstoppabledomains.exceptions.NamingServiceException
import com.unstoppabledomains.resolution.Resolution

data class ResolutionResult(val error: NamingServiceException?, val address: String?) {}

class AsyncResolution : AsyncTask<String, String, ResolutionResult>() {
    private val tool: DomainResolution = Resolution()

    override fun doInBackground(vararg params: String?): ResolutionResult {
        val domain =  params[0]
        val currency = params[1]
        return try {
            val address = this.tool.addr(domain, currency)
            ResolutionResult(null, address)
        } catch(err: NamingServiceException) {
            err.printStackTrace();
            ResolutionResult(err, null)
        }
    }
}
```
After you can use it in the following manner: 
```kotlin
val task = AsyncResolution().execute("brad.crypto", "ETH")
val result: ResolutionResult = task.get()
if (result.error != null) {
    makePaymentTo(result.address)
} else {
    displayErrorMessage(result.error)
}
```

Each Error is a NamingServiceException with it's own code and message, but for better UI or localization option you probably want to have your own wrapper.
NamingServiceException can be one of the following codes:

```
public enum NSExceptionCode {
  UnsupportedDomain,
  UnregisteredDomain,
  UnknownCurrency,
  RecordNotFound,
  BlockchainIsDown,
  UnknownError,
  IncorrectContractAddress,
  IncorrectMethodName,
  UnspecifiedResolver;
}
```


In order to retrieve any other information like ipfs hash you can find a method on Resolution object for such
### Example on how to retrieve ipfs hash with asyncTask

```kotlin
import android.os.AsyncTask
import com.unstoppabledomains.exceptions.NamingServiceException
import com.unstoppabledomains.resolution.Resolution

data class ResolutionResult(val error: NamingServiceException?, val address: String?) {}

class AsyncResolution : AsyncTask<String, String, ResolutionResult>() {
    private val tool: DomainResolution = Resolution()

    override fun doInBackground(vararg params: String?): ResolutionResult {
        val domain =  params[0]
        return try {
            val hash = this.tool.ipfsHash(domain)
            ResolutionResult(null, hash)
        } catch(err: NamingServiceException) {
            err.printStackTrace();
            ResolutionResult(err, null)
        }
    }
}
```

The hash you get looks like this: `Qme54oEzRkgooJbCDr78vzKAWcv6DDEZqRhhDyDtzgrZP6`

Using [ipfs](https://ipfs.io) technology you can optain the content by providing this hash to the ipfs.

With dweb you expect to get the static website fodler contains files such as html, css and js.
Each file has it's own hash but the hash stored in the domain usually points to a folder. 
In case if you are interested in obtaining only index.html or simillar files
we recommend you diving into the [ipfs docs](https://docs.ipfs.io/)

