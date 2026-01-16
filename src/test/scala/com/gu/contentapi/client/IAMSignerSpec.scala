package com.gu.contentapi.client

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class IAMSignerSpec extends AnyFlatSpec with Matchers {

  val credentials = AwsBasicCredentials.create("test-access-key", "test-secret-key")
  val credentialsProvider: AwsCredentialsProvider = StaticCredentialsProvider.create(credentials)
  val uri = new java.net.URI("https://example.com/test/path?a=b%20c&1=2%2C3")

  val signer = new IAMSigner(credentialsProvider, "eu-west-1")

  it should "return a map of IAM signed headers" in {
    val headers = Map(
      "a" -> "test-a",
      "1" -> "test-2",
    )
    val r = signer.addIAMHeaders(headers, uri)
    r.get("Authorization").getOrElse("").contains("SignedHeaders=1;a;accept;host;x") shouldBe true
  }
}

