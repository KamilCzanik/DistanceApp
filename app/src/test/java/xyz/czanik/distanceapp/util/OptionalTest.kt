package xyz.czanik.distanceapp.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OptionalTest {

    @Test
    fun `test hasValue returns true when value is NOT null`() = assertTrue(Optional(Unit).hasValue())

    @Test
    fun `test hasValue returns false when value is null`() = assertFalse(Optional(null).hasValue())

    @Test(expected = NullPointerException::class)
    fun `test requireValue throws exception when value is null`() = Optional<Unit>(null).requireValue()

    @Test
    fun `test requireValue returns value when value is NOT null`() = assertEquals(Unit, Optional(Unit).requireValue())

    @Test
    fun `test ofEmpty creates optional without value`() = assertEquals(Optional<Unit>(null), Optional.empty<Unit>())

    @Test
    fun `test of creates optional with value`() = assertEquals(Optional(Unit), Optional.of(Unit))

    @Test
    fun `test ofNullable creates optional with nullable value`() = assertEquals(Optional(Unit), Optional.of(Unit))
}