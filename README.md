### content-api-client-aws
_A library for helping with requests to an IAM-authorised AWS api-gateway_

[![Release](https://github.com/guardian/content-api-client-aws/actions/workflows/release.yml/badge.svg)](https://github.com/guardian/content-api-client-aws/actions/workflows/release.yml)

Creates the necessary authorisation headers based on a request.
E.g.
```scala
import com.gu.contentapi.client.{IAMSigner, IAMEncoder}

val signer = new IAMSigner(credentialsProvider, awsRegion))

//Query params must be encoded correctly
val queryString = IAMEncoder.encodeParams("testparam=with spaces")

val uri = URI.create(s"$endpoint/$path?$queryString")

val headers: Map[String,String] = signer.addIAMHeaders(Map.empty[String,String], uri)

//...create a request with uri and headers
```
