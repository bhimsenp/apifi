package apifi.parser

import apifi.parser.models.Model
import apifi.parser.models.Property
import apifi.parser.models.Request
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import io.swagger.v3.parser.OpenAPIV3Parser
import org.apache.commons.io.FileUtils

class RequestBodyParserTest : DescribeSpec({

    describe("Request Body Parser") {
        it("should parse request body") {
            val file = FileUtils.getFile("src", "test-res", "parser", "models", "with-separate-schema.yml").readText().trimIndent()
            val openApi = OpenAPIV3Parser().readContents(file).openAPI
            val request = RequestBodyParser.parse(openApi.paths["/pets"]?.post?.requestBody, "showById")
            request?.first shouldBe Request("kotlin.Array<Pet>", listOf("application/json"))
            request?.second shouldBe emptyList()
        }

        it("should parse request body with inline body") {
            val file = FileUtils.getFile("src", "test-res", "parser", "models", "with-inline-request-response-schema.yml").readText().trimIndent()
            val openApi = OpenAPIV3Parser().readContents(file).openAPI
            val request = RequestBodyParser.parse(openApi.paths["/pets"]?.post?.requestBody, "showById")
            request?.first shouldBe Request("kotlin.Array<ShowByIdRequest>", listOf("application/json"))
            request?.second shouldBe listOf(
                    Model("ShowByIdRequest", listOf(
                            Property("id", "kotlin.Long", false),
                            Property("name", "kotlin.String", false),
                            Property("tags", "kotlin.Array<kotlin.String>", true)
                    ))
            )
        }

        it("should generate request for multipart content type") {
            val file = FileUtils.getFile("src", "test-res", "parser", "request", "with-multipart-content-type.yml").readText().trimIndent()
            val openApi = OpenAPIV3Parser().readContents(file).openAPI
            val request = RequestBodyParser.parse(openApi.paths["/pet/{id}/uploadDoc"]?.post?.requestBody, "uploadDocument")
            request?.first shouldBe Request("io.micronaut.http.multipart.CompleteFileUpload", listOf("multipart/form-data"))
            request?.second shouldBe emptyList()
        }
    }
})