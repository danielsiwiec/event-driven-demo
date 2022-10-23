rootProject.name = "event-driven-demo"
include("services:catalogue")
include("services:customers")
include("services:email")
include("services:orders")
include("services:payments")
include("services:shipments")
include("tests:e2e")
include("tests:performance")