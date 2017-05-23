package com.seppius.i18n.plurals;
/**
 * Plural rules for the following locales and languages:
 *
 * Locales: ru
 *
 * Languages:
 *  Russian (ru)
 *
 * Rules:
 *  one → n is mod 1;
 *  two → n is mod 2-4;
 *  other → everything else AND 11-20
 *
 * Reference CLDR Version 1.9 beta (2010-11-16 21:48:45 GMT)
 * @see http://unicode.org/repos/cldr-tmp/trunk/diff/supplemental/language_plural_rules.html
 * @see http://unicode.org/repos/cldr/trunk/common/supplemental/plurals.xml
 * @see plurals.xml (local copy)
 *
 * @package    I18n_Plural
 * @category   Plural Rules
 * @author     Korney Czukowski
 * @copyright  (c) 2011 Korney Czukowski
 * @license    MIT License
 */

/**
 * Converted to Java by Sam Marshak, 2012 
 */

/**
 * Russian by Rinon Ninqueon, 2017
 */
final class PluralRules_Russian extends PluralRules
{
	public int quantityForNumber(final int count)
	{
		final int rem10 = count % 10;
		final int rem100 = count % 100;

		if (rem10 == 1 && (rem100 < 10 || rem100 > 20))
		{
			return QUANTITY_ONE;
		}
		else if ((rem10 >= 2 && rem10 <= 4) && (rem100 < 10 || rem100 > 20))
		{
			return QUANTITY_TWO;
		}
		else
		{
			return QUANTITY_OTHER;
		}
	}
}