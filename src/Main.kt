private val ones = listOf(
		"",
		"jeden",
		"dva",
		"tri",
		"štyri",
		"päť",
		"šesť",
		"sedem",
		"osem",
		"deväť",
		"desať",
		"jedenásť",
		"dvanásť",
		"trinásť",
		"štrnásť",
		"pätnásť",
		"šestnásť",
		"sedemnásť",
		"osemnásť",
		"devätnásť"
)

private val prefixes = listOf(
		"m",
		"b",
		"tr",
		"kvadr",
		"kvint",
		"sext",
		"sept",
		"okt",
		"non",
		"dec",
		"undec",
		"duodec",
		"tredec",
		"kvadrodec",
		"kvindec",
		"sexdec",
		"septendec",
		"oktodec",
		"novemdec",
		"duodec"
)

private val malePostfixes = listOf(
		"ilión",
		"ilióny",
		"iliónov"
)

private val femalePostfixes = listOf(
		"iliarda",
		"iliardy",
		"iliárd"
)


/**
 * Converts an Int bundle into a word. A bundle must be between 0 and 999 999.
 */
private fun convertBundle(bundle: Int): String {
	if (bundle >= 1_000_000) throw IllegalArgumentException("Bundle must not be bigger than 999 999! Bundle = $bundle")
	if (bundle < 0) throw IllegalArgumentException("Bundle must not be negative! Bundle = $bundle")

	return when (bundle) {
		in 0..19 -> ones[bundle]
		in 20..49 -> ones[bundle / 10] + "dsať" + ones[bundle % 10]
		in 50..99 -> ones[bundle / 10] + "desiat" + ones[bundle % 10]
		in 100..199 -> "sto" + convertBundle(bundle % 100)
		in 200..299 -> "dvesto" + convertBundle(bundle % 100)
		in 300..999 -> ones[bundle / 100] + "sto" + convertBundle(bundle % 100)
		in 1000..1999 -> "tisíc" + convertBundle(bundle % 1000)
		in 2000..2999 -> "dvetisíc" + convertBundle(bundle % 1000)
		else -> convertBundle(bundle / 1000) + "tisíc" + convertBundle(bundle % 1000)
	}
}

/**
 * First grade -> "m-ilión"
 * Second grade -> "dva m-ilióny"
 * Third grade -> "päť m-iliónov"
 */
enum class Grade {
	FIRST,
	SECOND,
	THIRD
}


/**
 * Male -> "milión", "milióny", "miliónov"
 * Female -> "miliarda", "miliardy", "miliárd"
 */
enum class Gender {
	MALE,
	FEMALE
}


/**
 * Generates the base for a number.
 * Example: generates <i>"milión"</i> if 0 is passed as index and <i>Grade.FIRST</i> as grade. This is because if you
 * have for example the number 41 501 101, "41" is the first bundle right after the "special part" of the number
 * (the two joined bundles at the end).
 */
private fun generateBase(index: Int, grade: Grade, gender: Gender): String {

	return prefixes[index] + when (gender) {
		Gender.MALE -> malePostfixes[when (grade) {
			Grade.FIRST -> 0
			Grade.SECOND -> 1
			Grade.THIRD -> 2
		}]

		Gender.FEMALE -> femalePostfixes[when (grade) {
			Grade.FIRST -> 0
			Grade.SECOND -> 1
			Grade.THIRD -> 2
		}]
	}
}


/**
 * Separates the number in the form of String into a list of chunks of three numbers, called "bundles". This is
 * because the fact that the Slovak numbering system also does that.
 */
private fun separateIntoBundles(n: String): List<Int> {
	return n.reversed().chunked(3).map { it.reversed().toInt() }.reversed()
}

/**
 * Converts the actual number in the form of String into a worded number in the Slovak language.
 */
fun convertNumber(n: String): String {
	if (n.startsWith("0")) throw IllegalArgumentException("Number should not start with 0.")

//	 Remove the underscores that were used for better readability of the number.
	val separated = separateIntoBundles(n.replace("_", ""))

	when (separated.size) {
		0 -> throw IllegalArgumentException("Please enter a number, not an empty String in the function convertNumber()")
		1 -> return convertBundle(separated[0])
		2 -> return convertBundle(separated[0] * 1000 + separated[1])
	}

	var output = ""

//	 Iterate the bundles list and leave the last two bundles alone
	for (i in 0 until separated.size - 2) {
		val bundle = separated[i]

		if (bundle == 0) {
			continue
		}

//		 This converts the normal index into the special bundle index.
		val bundleIndex = (separated.size - i - 3) / 2

//		 DEBUG
//		println("Converting $bundle, bundleIndex is $bundleIndex")

		val gender = when (i % 2 == 0) {
			// It's even, thus MALE
			true -> Gender.MALE
			// It's odd, thus FEMALE
			false -> Gender.FEMALE
		}

		output += when (bundle) {
			1 -> if (gender == Gender.MALE) "jeden" else "jedna"
			else -> convertBundle(bundle)
		} + " " + generateBase(
				bundleIndex,

				when (bundle) {
					in 0..1 -> Grade.FIRST
					in 2..4 -> Grade.SECOND
					else -> Grade.THIRD
				},

				gender) + " "
	}

//	 Handle the last two bundles as one bundle, because that's how the Slovak system works. :)
	output += convertBundle(separated[separated.size - 2] * 1000 + separated[separated.size - 1]).trim()

	return output
}


fun main() {
	
	println(convertNumber("444_000_111_001_001_420_666"))

}