package org.tahoe.lafs.extension

/** Helpers for dealing with Result */
object ResultExtensions {
  /** Monadic transformation of a Result */
  fun <I, O> Result<I>.flatMap(f: (I) -> Result<O>): Result<O> =
      if (this.isFailure) {
        val error = this.exceptionOrNull()
        if (error != null) {
          Result.failure<O>(error)
        } else {
          Result.failure<O>(RuntimeException("Failure result without an exception"))
        }
      } else {
        val result = this.getOrNull()
        if (result != null) {
          try {
            f(result)
          } catch (e: Throwable) {
            Result.failure<O>(e)
          }
        } else {
          Result.failure<O>(IllegalArgumentException("flatMapping a null result"))
        }
      }

  /** Given two results, returns a pair of their contents if they are both successful */
  fun <A, B> zip(a: Result<A>, b: Result<B>): Result<Pair<A, B>> =
      a.flatMap { aVal -> b.flatMap { bVal -> Result.success(Pair(aVal, bVal)) } }
}

