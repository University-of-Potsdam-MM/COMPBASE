package uzuzjmd.scalahacks

import java.lang.reflect.Constructor

import de.unipotsdam.ontologypersistence.owl.access.OntologyManager

object ScalaHacksInScala {
  /**
   * Hilfsfunktion, um eine generisch spezifizierte Klasse zu instantiieren
   */
  def instantiateDao[A](clazz: java.lang.Class[A])(comp: OntologyManager, identifier: String): Any = {
    val constructors = clazz.getDeclaredConstructors()
    val constructor = constructors.head
    constructor.setAccessible(true);
    constructor.getGenericParameterTypes().length match {
      case 2 =>
        try {
          return constructor.newInstance(comp, identifier)
        } catch {
          case e: Exception =>
            e.printStackTrace()
            println("hello my friend")
        }
      case 3 => try {
        return constructor.newInstance(comp, identifier, null)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          println("hello my friend")
      }
      case 4 => try {
        return constructor.newInstance(comp, identifier, null, null)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          println("hello my friend")
      }
      case 5 => try {
        return constructor.newInstance(comp, identifier, null, null, null)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          println("hello my friend")
      }
      case 6 => try {
        return constructor.newInstance(comp, identifier, null, null, null, null)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          println("hello my friend")
      }

      case 7 => try {
        return constructor.newInstance(comp, identifier, null, null, null, null, null)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          println("hello my friend")
      }

      case 8 => try {
        return constructor.newInstance(comp, identifier, null, null, null, null, null, null)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          println("hello my friend")
      }
      case 9 => try {
        return constructor.newInstance(comp, identifier, null, null, null, null, null, null, null)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          println("hello my friend")
      }

      case 10 => try {
        return constructor.newInstance(comp, identifier, null, null, null, null, null, null, null, null)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          println("hello my friend")
      }

    }

    throw new Error("error with instantiation of class" + clazz)
  }

  def sameNumberofArguments(args: AnyRef*)(constructor: Constructor[_]): Boolean = {
    if (constructor.getGenericParameterTypes().length != args.length) {
      //      throw new Error("wrong number of arguments matched")
      return false
    }
    return true
  }
}