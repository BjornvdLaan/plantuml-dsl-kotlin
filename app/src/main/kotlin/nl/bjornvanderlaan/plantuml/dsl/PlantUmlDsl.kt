package nl.bjornvanderlaan.plantuml.dsl

abstract class PlantUmlElement {
    protected val children = mutableListOf<PlantUmlElement>()

    protected fun <T: PlantUmlElement> addChild(child: T) {
        children.add(child)
    }

    protected fun <T: PlantUmlElement> addChild(child: T, init: T.() -> Unit) {
        child.init()
        this.addChild(child)
    }

    abstract override fun toString(): String
}

enum class AttributeVisibility { PUBLIC, PRIVATE, PROTECTED }

abstract class Attribute(private val first: String, private val second: String? = null): PlantUmlElement() {
    private var visibility: AttributeVisibility = AttributeVisibility.PUBLIC

    fun setVisibility(visibility: AttributeVisibility): Attribute {
        this.visibility = visibility
        return this
    }

    protected fun getModifier() =
        when(this.visibility) {
            AttributeVisibility.PUBLIC -> "+"
            AttributeVisibility.PRIVATE -> "-"
            AttributeVisibility.PROTECTED -> "*"
        }

    abstract override fun toString(): String
}

class Field(private val name: String, private val dataType: String? = null): Attribute(name, dataType) {
    override fun toString() = "${getModifier()} $name ${if(dataType != null) ": $dataType" else ""}"
}

class Method(private val name: String, private val returnType: String? = null): Attribute(name, returnType) {
    override fun toString() = "${getModifier()} $name() ${if(returnType != null) ": $returnType" else ""}"
}

enum class RelationshipType { EXTENSION, COMPOSITION, AGGREGATION, ASSOCIATION }

class Relationship(private val left: String, private val right: String, private val type: RelationshipType, private val label: String? = null): PlantUmlElement() {
    override fun toString() = "$left ${getArrow()} $right ${if (label != null) ": $label" else ""}"

    private fun getArrow() =
        when(this.type) {
            RelationshipType.EXTENSION -> "<|--"
            RelationshipType.COMPOSITION -> "*--"
            RelationshipType.AGGREGATION -> "o--"
            RelationshipType.ASSOCIATION -> "<--"
        }
}

class Class(private val name: String, private val identifier: String): PlantUmlElement() {
    private fun addAttributes(attributes: List<Attribute>, visibility: AttributeVisibility)
        = attributes.forEach { addChild(it.setVisibility(visibility)) }

    fun public(vararg attributes: Attribute) = addAttributes(attributes.toList(), AttributeVisibility.PUBLIC)
    fun private(vararg attributes: Attribute) = addAttributes(attributes.toList(), AttributeVisibility.PRIVATE)

    override fun toString() = "class \"${this.name}\" as ${this.identifier} {\n\t${children.joinToString("\n\t")}\n}"
}

class ClassDiagram: PlantUmlElement() {
    fun clazz(name: String, identifier: String = name.replace(" ", ""), init: Class.() -> Unit) =
        addChild(Class(name, identifier), init)

    fun relationship(left: String, right: String, type: RelationshipType, label: String? = null) =
        addChild(Relationship(left, right, type, label))

    override fun toString() = "@startuml ldm\n\t${children.joinToString("\n\n")}\n@enduml"
}

fun classDiagram(init: ClassDiagram.() -> Unit) = ClassDiagram().apply(init)