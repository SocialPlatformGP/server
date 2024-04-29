package com.example.routes.post

import com.example.data.requests.AddTagRequest
import com.example.repository.PostRepository
import com.example.utils.DataError
import com.example.utils.EndPoint
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.postTags(
    postRepository: PostRepository
) {
    post(EndPoint.Post.Tags.InsertTag.route) {
        val request = call.receiveNullable<AddTagRequest>() ?: return@post call.respond(HttpStatusCode.BadRequest, DataError.Network.BAD_REQUEST)
        val wasAcknowledged = postRepository.insertTag(request.toEntity())
        call.respond(HttpStatusCode.OK, wasAcknowledged)

    }
    post(EndPoint.Post.Tags.GetAllTags.route) {
        val request = call.receiveNullable<String>()?:return@post call.respond(HttpStatusCode.BadRequest, DataError.Network.BAD_REQUEST)
        val response = postRepository.getTags(request )
        call.respond(HttpStatusCode.OK, response)

    }
}


