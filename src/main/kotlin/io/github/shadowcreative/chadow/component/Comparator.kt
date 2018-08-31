package io.github.shadowcreative.chadow.component


/**
 * 문자 관련 유틸
 */
object CharUtil {
    fun isEnglish(ch: Char): Boolean {
        return ch.toInt() >= 'A'.toInt() && ch.toInt() <= 'Z'.toInt() || ch.toInt() >= 'a'.toInt() && ch.toInt() <= 'z'.toInt()
    }

    fun isKorean(ch: Char): Boolean {
        return ch.toInt() >= Integer.parseInt("AC00", 16) && ch.toInt() <= Integer.parseInt("D7A3", 16)
    }

    fun isNumber(ch: Char): Boolean {
        return ch.toInt() >= '0'.toInt() && ch.toInt() <= '9'.toInt()
    }

    fun isSpecial(ch: Char): Boolean {
        return (ch.toInt() >= '!'.toInt() && ch.toInt() <= '/'.toInt() // !"#$%&'()*+,-./

                || ch.toInt() >= ':'.toInt() && ch.toInt() <= '@'.toInt() //:;<=>?@

                || ch.toInt() >= '['.toInt() && ch.toInt() <= '`'.toInt() //[\]^_`

                || ch.toInt() >= '{'.toInt() && ch.toInt() <= '~'.toInt()) //{|}~
    }

/*
    /**
     * 한글 > 영어 > 숫자 > 특수문자 순서 정렬 객체
     */
    class OrderingByKoreanEnglishNumbuerSpecial : Comparator<String>
    {
        private val REVERSE = -1
        private val LEFT_FIRST = -1
        private val RIGHT_FIRST = 1

        override fun comp

        /**
         * 한글 > 영어 > 숫자 > 특수문자 순서 비교 함수
         * @param left
         * @param right
         * @return
         */
        fun compare(left: String, right: String): Int {
            var left = left
            var right = right

            left = left.toUpperCase().replace(" ", "")
            right = right.toUpperCase().replace(" ", "")

            val leftLen = left.length
            val rightLen = right.length
            val minLen = Math.min(leftLen, rightLen)

            for (i in 0 until minLen) {
                val leftChar = left[i]
                val rightChar = right[i]

                if (leftChar != rightChar) {
                    return if (isKoreanAndEnglish(leftChar, rightChar)
                            || isKoreanAndNumber(leftChar, rightChar)
                            || isEnglishAndNumber(leftChar, rightChar)
                            || isKoreanAndSpecial(leftChar, rightChar)) {
                        (leftChar - rightChar) * REVERSE
                    } else if (isEnglishAndSpecial(leftChar, rightChar) || isNumberAndSpecial(leftChar, rightChar)) {
                        if (isEnglish(leftChar) || isNumber(leftChar)) {
                            LEFT_FIRST
                        } else {
                            RIGHT_FIRST
                        }
                    } else {
                        leftChar - rightChar
                    }
                }
            }

            return leftLen - rightLen
        }

        private fun isKoreanAndEnglish(ch1: Char, ch2: Char): Boolean {
            return isEnglish(ch1) && isKorean(ch2) || isKorean(ch1) && isEnglish(ch2)
        }

        private fun isKoreanAndNumber(ch1: Char, ch2: Char): Boolean {
            return isNumber(ch1) && isKorean(ch2) || isKorean(ch1) && isNumber(ch2)
        }

        private fun isEnglishAndNumber(ch1: Char, ch2: Char): Boolean {
            return isNumber(ch1) && isEnglish(ch2) || isEnglish(ch1) && isNumber(ch2)
        }

        private fun isKoreanAndSpecial(ch1: Char, ch2: Char): Boolean {
            return isKorean(ch1) && isSpecial(ch2) || isSpecial(ch1) && isKorean(ch2)
        }

        private fun isEnglishAndSpecial(ch1: Char, ch2: Char): Boolean {
            return isEnglish(ch1) && isSpecial(ch2) || isSpecial(ch1) && isEnglish(ch2)
        }

        private fun isNumberAndSpecial(ch1: Char, ch2: Char): Boolean {
            return isNumber(ch1) && isSpecial(ch2) || isSpecial(ch1) && isNumber(ch2)
        }
    } */
}
