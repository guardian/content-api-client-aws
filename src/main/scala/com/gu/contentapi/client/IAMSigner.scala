package com.gu.contentapi.client

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.http.{SdkHttpFullRequest, SdkHttpMethod}
import software.amazon.awssdk.http.auth.aws.signer.{AwsV4HttpSigner, AwsV4FamilyHttpSigner}
import software.amazon.awssdk.http.auth.spi.signer.SignRequest
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity

import java.net.URI
import scala.jdk.CollectionConverters._

/**
  * For api-gateway authorization.
  */
class IAMSigner(credentialsProvider: AwsCredentialsProvider, awsRegion: String) {

  private val serviceName = "execute-api"
  private val signer = AwsV4HttpSigner.create()

  def credentials = credentialsProvider.resolveCredentials()

  /**
    * Returns the given set of headers, updated to include AWS sig4v signed headers based on the request and the credentials
    *
    * @param headers  Current set of request headers
    * @param uri      Request URI, including params
    * @return         Updated set of headers, including the authorisation headers
    */
  def addIAMHeaders(headers: Map[String, String], uri: URI): Map[String, String] = {

    val queryParams: Map[String, java.util.List[String]] =
      Option(uri.getQuery)
        .map(_.split("&").toList.flatMap { s =>
          s.split("=").toList match {
            case k :: v :: Nil => Some(k -> java.util.Collections.singletonList(v))
            case _ => None
          }
        }.toMap)
        .getOrElse(Map.empty)

    val unsignedRequest: SdkHttpFullRequest = {
      //api-gateway will break the compressed json response if we don't supply an accept header
      val headersWithAccept =
        if (headers.contains("accept") || headers.contains("Accept")) headers
        else headers + ("accept" -> "application/json")

      val reqBuilder = SdkHttpFullRequest.builder()
        .method(SdkHttpMethod.GET)
        .uri(new java.net.URI(s"${uri.getScheme}://${uri.getHost}"))
        .encodedPath(uri.getPath)

      headersWithAccept.foreach { case (k, v) => reqBuilder.putHeader(k, v) }
      queryParams.foreach { case (k, v) => reqBuilder.putRawQueryParameter(k, v) }

      reqBuilder.build()
    }

    val signedRequest = signer.sign { r: SignRequest.Builder[AwsCredentialsIdentity] =>
      r.identity(credentials)
        .request(unsignedRequest)
        .putProperty(AwsV4HttpSigner.REGION_NAME, awsRegion)
        .putProperty(AwsV4FamilyHttpSigner.SERVICE_SIGNING_NAME, serviceName)
    }

    signedRequest.request().headers().asScala
      .map { case (k, v) => k -> v.asScala.headOption.getOrElse("") }
      .toMap
  }
}
