  Feature: Orders

    Scenario: Create Order with good SKUs
      When I create a following order for customer 1:
        | sku | quantity |
        | 1   | 1        |
        | 2   | 1        |
        | 4   | 2        |
      Then the response should be 200
      And 2 emails should be sent out
      And a payment should be submitted
      And 1 shipment should be dispatched

    Scenario: Create Order with a missing SKU
      When I create a following order for customer 1:
        | sku | quantity |
        | 99  | 1        |
      Then the response should be 400
      And a payment should not be submitted
      And a shipment should not be dispatched



