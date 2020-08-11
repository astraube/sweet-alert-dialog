package com.github.astraube

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.content.res.XmlResourceParser
import android.util.AttributeSet
import android.util.Xml
import android.view.animation.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

object OptAnimationLoader {
    @JvmStatic
    @Throws(NotFoundException::class)
    fun loadAnimation(context: Context, id: Int): Animation? {
        var parser: XmlResourceParser? = null
        return try {
            parser = context.resources.getAnimation(id)
            createAnimationFromXml(context, parser)
        } catch (ex: XmlPullParserException) {
            val rnf = NotFoundException(
                "Can't load animation resource ID #0x" + Integer.toHexString(id)
            )
            rnf.initCause(ex)
            throw rnf
        } catch (ex: IOException) {
            val rnf = NotFoundException(
                "Can't load animation resource ID #0x" + Integer.toHexString(id)
            )
            rnf.initCause(ex)
            throw rnf
        } finally {
            parser?.close()
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun createAnimationFromXml(
        c: Context,
        parser: XmlPullParser,
        parent: AnimationSet? = null,
        attrs: AttributeSet = Xml.asAttributeSet(parser)
    ): Animation? {
        var anim: Animation? = null

        // Make sure we are on a start tag.
        val depth = parser.depth
        var type: Int

        while ((parser.next().also {
                type = it
            }
                    != XmlPullParser.END_TAG || parser.depth > depth)
            && type != XmlPullParser.END_DOCUMENT
        ) {
            if (type != XmlPullParser.START_TAG) {
                continue
            }
            anim = getAnimation(c, parser, attrs)
            parent?.addAnimation(anim)
        }
        return anim
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun getAnimation(context: Context, parser: XmlPullParser, attrs: AttributeSet): Animation? {
        var anim: Animation? = null
        when (val name = parser.name) {
            "set" -> {
                anim = AnimationSet(context, attrs)
                createAnimationFromXml(context, parser, anim as AnimationSet?, attrs)
            }
            "alpha" -> {
                anim = AlphaAnimation(context, attrs)
            }
            "scale" -> {
                anim = ScaleAnimation(context, attrs)
            }
            "rotate" -> {
                anim = RotateAnimation(context, attrs)
            }
            "translate" -> {
                anim = TranslateAnimation(context, attrs)
            }
            else -> {
                anim = try {
                    Class.forName(name).getConstructor(
                        Context::class.java,
                        AttributeSet::class.java
                    ).newInstance(context, attrs) as Animation
                } catch (te: Exception) {
                    throw RuntimeException("Unknown animation name: " + parser.name + " error:" + te.message)
                }
            }
        }
        return anim
    }
}