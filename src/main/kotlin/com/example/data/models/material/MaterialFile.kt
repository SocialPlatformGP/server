package com.example.data.models.material

import com.example.data.responses.MaterialFileResponse
import com.example.data.responses.MaterialFolderResponse
import com.example.utils.now
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class MaterialFile(
    val communityId: String="",
    val name: String="",
    val size: String = "",
    val type: String="",
    val url: String="",
    val path : String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @BsonId
    val id : String = ObjectId().toString(),
    val localPath: String = "",
    ){
    fun toResponse() = MaterialFileResponse(
        name = name,
        type = type,
        size = size,
        url = url,
        createdAt = createdAt,
        id = id,
        path = path,
        localPath = localPath,
        communityId = communityId
    )
}
@Serializable
data class MaterialFolder(
    val communityId: String="",
    val name: String="",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @BsonId
    val id : String = ObjectId().toString(),
    val path: String = ""
){
    fun toResponse() = MaterialFolderResponse(
        name = name,
        createdAt = createdAt,
        id = id,
        path = path,
        communityId = communityId
    )
}
