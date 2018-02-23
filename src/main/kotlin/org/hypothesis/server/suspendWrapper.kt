@file:JvmName("suspend")
package org.hypothesis.server

import kotlinx.coroutines.experimental.runBlocking
import org.fejoa.chunkcontainer.BoxSpec
import org.fejoa.crypto.CryptoHelper
import org.fejoa.crypto.CryptoSettings
import org.fejoa.crypto.SecretKey
import org.fejoa.crypto.SymBaseCredentials
import org.fejoa.repository.*
import org.fejoa.storage.*
import org.fejoa.support.*

class AsyncCloseableWrapper(val asyncCloseable:AsyncCloseable)
class AsyncOutStreamWrapper(val asyncOutStream: AsyncOutStream)
class DatabaseStringEntry(var path: String,var content: String)

class CommitWrapper(val commit:Commit?){
    fun getHash():Hash = runBlocking {
        return@runBlocking commit!!.getHash()
    }
}

class RandomDataAccessWrapper(val randomDataAccess: RandomDataAccess) {
    fun write(buffer: kotlin.ByteArray): kotlin.Int = runBlocking {
        return@runBlocking randomDataAccess.write(buffer)
    }
    fun seek(position:Long) = runBlocking {
        return@runBlocking randomDataAccess.seek(position)
    }
    fun close() = runBlocking {
        return@runBlocking null
    }
    fun delete(position: Long,length:Long) = runBlocking { return@runBlocking randomDataAccess.delete(position, length) }

}


class RepoWrapper(val repository: Repository) {

    fun open(path: String, mode: RandomDataAccess.Mode): RandomDataAccessWrapper = runBlocking {
        return@runBlocking RandomDataAccessWrapper(repository.open(path, mode))
    }
    fun readBytes(path: String):ByteArray = runBlocking {
        return@runBlocking repository.readBytes(path)
    }
    fun putBytes(path: String, data:ByteArray) = runBlocking {
        return@runBlocking repository.putBytes(path,data)
    }
    fun commit(message: ByteArray, signature: CommitSignature): Hash = runBlocking {
        return@runBlocking repository.commit(message,signature)
    }
    fun getHead():Hash = runBlocking {
        return@runBlocking repository.getHead()
    }
    fun getHeadCommit():CommitWrapper = run{
        return@run CommitWrapper(repository.getHeadCommit())
    }
    fun getRepositoryRef(): RepositoryRef = runBlocking {
        return@runBlocking repository.getRepositoryRef()
    }
    fun open(branch: String, ref: RepositoryRef, branchBackend: StorageBackend.BranchBackend,
    crypto: SymBaseCredentials?): RepoWrapper= runBlocking {
        return@runBlocking RepoWrapper(Repository.open(branch, ref, branchBackend, crypto))
    }
    fun listFiles(path: String): Collection<String> = runBlocking{
        return@runBlocking repository.listFiles(path)
    }

    fun listDirectories(path: String): Collection<String>  = runBlocking{
        return@runBlocking repository.listDirectories(path)
    }

}

open class suspendFun constructor(val storageBackend: StorageBackend, val secretKey: SecretKey) {


    companion object {
        val cleanUpList: MutableList<String> = ArrayList()
        val settings = CryptoSettings.default
        fun create(): suspendFun = runBlocking {
            val secretKey = CryptoHelper.crypto.generateSymmetricKey(settings.symmetric)
            val storageBackend = platformCreateStorage("")
            return@runBlocking suspendFun(storageBackend, secretKey)
        }
    }
    val symCredentials = SymBaseCredentials(secretKey!!, settings.symmetric.algo)
    fun storageOpen(dirName:String,branch:String): StorageBackend.BranchBackend = runBlocking { storageBackend?.let {
        val branchBackend = if (it.exists(dirName,branch)) {
            it.open(dirName,branch)
        }
        else{
            it.create(dirName,branch)
        }
        cleanUpList.add(dirName)

        return@let branchBackend
    }?: throw Exception("error")}

    protected fun getRepoConfig(): RepositoryConfig {
        val seed = ByteArray(10) // just some zeros
        val hashSpec = HashSpec.createCyclicPoly(HashSpec.HashType.FEJOA_CYCLIC_POLY_2KB_8KB, seed)

        val boxSpec = BoxSpec(
                encInfo = BoxSpec.EncryptionInfo(BoxSpec.EncryptionInfo.Type.PARENT),
                zipType = BoxSpec.ZipType.DEFLATE,
                zipBeforeEnc = true
        )

        return RepositoryConfig(
                hashSpec = hashSpec,
                boxSpec = boxSpec
        )
    }
    fun createRepository(dirName:String, branch:String): RepoWrapper = runBlocking {
        val backend = storageOpen(dirName, branch)
        return@runBlocking RepoWrapper(Repository.create(branch, backend,getRepoConfig(), SymBaseCredentials(secretKey!!,settings.symmetric.algo)))
    }
    fun add(database: RepoWrapper, content: MutableMap<String, DatabaseStringEntry>, entry: DatabaseStringEntry) = runBlocking{
        content.put(entry.path, entry)
        database.putBytes(entry.path, entry.content.toUTF())
    }
    fun remove(database: Repository, content: MutableMap<String, DatabaseStringEntry>, path: String) = runBlocking{
        if (content.containsKey(path)) {
            content.remove(path)
            database.remove(path)
        }
    }
    fun containsContent(database: RepoWrapper, content: Map<String, DatabaseStringEntry>)= runBlocking {
        for (entry in content.values) {
            val bytes = database.readBytes(entry.path)
           //assertNotNull(bytes)
           // assertEquals(entry.content, bytes.toUTFString())
        }
        //assertEquals(content.size, countFiles(database, ""))
    }
    val simpleCommitSignature = object : CommitSignature {
        override suspend fun signMessage(message: ByteArray, rootHashValue: HashValue, parents: Collection<HashValue>): ByteArray {
            return message
        }

        override suspend fun verifySignedMessage(signedMessage: ByteArray, rootHashValue: HashValue, parents: Collection<HashValue>): Boolean {
            return true
        }

    }


}