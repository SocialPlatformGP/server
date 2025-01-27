package com.example.data.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Reply (
    @BsonId
    val id: String = ObjectId().toString(),
    val authorID: String = "",
    val postId: String = "",
    val parentReplyId: String? = "",
    val content: String = "",
    val votes: Int = 0,
    val depth: Int = 0,
    val createdAt: Long = LocalDateTime.now().toInstant(TimeZone.UTC).epochSeconds,
    val deleted: Boolean = false,
    val upvoted: List<String> = emptyList(),
    val downvoted: List<String> = emptyList(),
    val authorName: String = "",
    val authorImageLink: String = "",
    val collapsed: Boolean = false,
    val editStatus: Boolean = false
)