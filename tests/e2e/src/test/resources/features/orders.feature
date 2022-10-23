  Feature: Orders

    Scenario: Create Order with good SKUs
      When I create a following order for customer 1:
        | sku | quantity |
        | 1   | 1        |
        | 2   | 1        |
        | 4   | 2        |
      Then the response should be 200
      And 1 email should be sent out
      And a payment should be sent out

    Scenario: Create Order with a missing SKU
      When I create a following order for customer 1:
        | sku | quantity |
        | 99  | 1        |
      Then the response should be 400
      And a payment should not be sent out



