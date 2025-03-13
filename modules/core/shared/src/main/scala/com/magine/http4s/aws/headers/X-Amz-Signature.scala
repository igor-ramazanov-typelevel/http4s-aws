/*
 * Copyright 2025 Magine Pro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.magine.http4s.aws.headers

import org.http4s.Header
import org.http4s.ParseResult
import org.http4s.Request
import org.typelevel.ci.*

final case class `X-Amz-Signature`(value: String)

object `X-Amz-Signature` {
  def get[F[_]](request: Request[F]): Option[`X-Amz-Signature`] =
    request.headers.get[`X-Amz-Signature`]

  def parse(s: String): ParseResult[`X-Amz-Signature`] =
    Right(apply(s))

  def put[F[_]](value: String)(request: Request[F]): Request[F] =
    request.putHeaders(apply(value))

  def putIfAbsent[F[_]](value: String)(request: Request[F]): Request[F] =
    if (request.headers.contains[`X-Amz-Signature`]) request else put(value)(request)

  def putQueryParam[F[_]](value: String)(request: Request[F]): Request[F] =
    request.withUri(request.uri.withQueryParam("X-Amz-Signature", value))

  implicit val headerInstance: Header[`X-Amz-Signature`, Header.Single] =
    Header.createRendered(ci"X-Amz-Signature", _.value, parse)
}
