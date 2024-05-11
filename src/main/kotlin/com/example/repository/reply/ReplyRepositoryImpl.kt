package com.example.repository.reply

import com.example.data.models.post.Post
import com.example.data.models.reply.Reply
import com.example.data.requests.ReplyRequest
import com.example.data.responses.ReplyResponse
import com.example.data.source.remote.ContentModerationRemoteDataSource
import org.litote.kmongo.addToSet
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.pull
import org.litote.kmongo.setValue

class ReplyRepositoryImpl(
    private val db: CoroutineDatabase,
    private val moderationRemoteSSource: ContentModerationRemoteDataSource,
) : ReplyRepository {
    private val replyCollection = db.getCollection<Reply>()
    private val postCollection = db.getCollection<Post>()

    override suspend fun createReply(response: ReplyResponse): Boolean {
        val post = postCollection.findOne(Post::id eq response.postId) ?: return false
        postCollection.updateOne(
            Post::id eq response.postId, setValue(
                Post::replyCount, post.replyCount + 1
            )
        )
        return replyCollection.insertOne(response.toEntity()).wasAcknowledged()
    }

    override suspend fun fetchReplies(request: ReplyRequest.FetchRequest): List<Reply> {
        val replies = replyCollection.find().toList().filter {
            it.postId == request.postId
        }
        return replies
    }

    override suspend fun updateReply(request: ReplyRequest.UpdateRequest): Boolean {
        val reply = replyCollection.findOne(Reply::id eq request.replyId) ?: return false
        return replyCollection.updateOne(Reply::id eq reply.id, reply.copy(content = request.replyContent))
            .wasAcknowledged()
    }

    override suspend fun deleteReply(request: ReplyRequest.DeleteRequest): Boolean {
        val reply = replyCollection.findOne(Reply::id eq request.replyId) ?: return false
        return replyCollection.deleteOne(Reply::id eq reply.id).wasAcknowledged()
    }

    override suspend fun upvoteReply(request: ReplyRequest.UpvoteRequest): Boolean {
        if (request.userId.isBlank()) {
            return false
        }
        val reply = replyCollection.findOne(Reply::id eq request.replyId) ?: return false
        val userAlreadyVoted = reply.upvoted.contains(request.userId)
        if (userAlreadyVoted) {
            replyCollection.updateOne(
                Reply::id eq request.replyId, pull(Reply::upvoted, request.userId)
            )
            replyCollection.updateOne(
                Reply::id eq request.replyId, setValue(
                    Reply::votes, reply.votes - 1
                )
            )
            return true
        } else {
            if (reply.downvoted.contains(request.userId)) {
                replyCollection.updateOne(
                    Reply::id eq request.replyId, pull(Reply::downvoted, request.userId)
                )
                replyCollection.updateOne(
                    Reply::id eq request.replyId, setValue(
                        Reply::votes, reply.votes + 2
                    )
                )
            } else {
                replyCollection.updateOne(
                    Reply::id eq request.replyId, setValue(
                        Reply::votes, reply.votes + 1
                    )
                )
            }
            replyCollection.updateOne(
                Reply::id eq request.replyId, addToSet(Reply::upvoted, request.userId)
            )
            return true
        }
    }

    override suspend fun downvoteReply(request: ReplyRequest.DownvoteRequest): Boolean {
        if (request.userId.isBlank()) {
            return false
        }
        val reply = replyCollection.findOne(Reply::id eq request.replyId) ?: return false
        val userAlreadyVoted = reply.downvoted.contains(request.userId)
        if (userAlreadyVoted) {
            replyCollection.updateOne(
                Reply::id eq request.replyId, pull(Reply::downvoted, request.userId)
            )
            replyCollection.updateOne(
                Reply::id eq request.replyId, setValue(
                    Reply::votes, reply.votes + 1
                )
            )
            return true
        } else {
            if (reply.upvoted.contains(request.userId)) {
                replyCollection.updateOne(
                    Reply::id eq request.replyId, pull(Reply::upvoted, request.userId)
                )
                replyCollection.updateOne(
                    Reply::id eq request.replyId, setValue(
                        Reply::votes, reply.votes - 2
                    )
                )
            } else {
                replyCollection.updateOne(
                    Reply::id eq request.replyId, setValue(
                        Reply::votes, reply.votes - 1
                    )
                )
            }
            replyCollection.updateOne(
                Reply::id eq request.replyId, addToSet(Reply::downvoted, request.userId)
            )
            return true
        }
    }

    override suspend fun reportReply(request: ReplyRequest.ReportRequest): Boolean {
        val reply = replyCollection.findOne(Reply::id eq request.replyId) ?: return false
        val result = moderationRemoteSSource.validateText(reply.content)
        println("\n\n\n\n\n\nresult: $result\n\n\n\n\n\n")
        return true
    }
}