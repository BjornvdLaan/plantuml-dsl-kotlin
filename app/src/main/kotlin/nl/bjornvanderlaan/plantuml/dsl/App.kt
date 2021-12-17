package nl.bjornvanderlaan.plantuml.dsl


fun main() {
    val myDiagram = classDiagram {
        clazz("Person") {
            public (
                Field("id", "Integer"),
                Method("getName", "String")
            )
        }

        clazz("Customer") {
            public (
                Field("id", "Integer"),
                Field("emailAddress", "String"),
                Method("getName", "String"),
                Method("getAccountBalance", "Integer"),
                Method("verifyPassword", "Boolean")
            )

            private (
                Field("name", "String"),
                Field("password", "String")
            )
        }

        clazz("Wallet") {
            public (
                Field("id", "Integer"),
                Field("customerId", "Integer"),
                Method("getBalance", "Integer")
            )
        }

        clazz("Order") {
            public (
                Field("id", "Integer"),
                Field("customerId", "Integer"),
                Method("place", "Integer"),
                Method("getTotalPrice", "Float")
            )
        }

        clazz("OrderItem") {
            public (
                Field("id", "Integer"),
                Field("orderId", "Integer"),
                Method("getDescription", "String")
            )

            private (
                Field("description", "String"),
                Field("price", "Float")
            )
        }

        relationship("Person", "Customer", RelationshipType.EXTENSION, "extends")
        relationship("Customer", "Wallet", RelationshipType.AGGREGATION, "belongs to")

        relationship("Order", "OrderItem", RelationshipType.COMPOSITION, "is part of")

        relationship("Order", "Customer", RelationshipType.ASSOCIATION, "places")
    }

    println(myDiagram)
}
