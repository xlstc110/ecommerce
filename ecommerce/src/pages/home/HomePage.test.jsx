import { it, expect, describe, vi, beforeEach } from "vitest";
import { render, screen, within } from "@testing-library/react";
import axios from "axios";
import { HomePage } from "./HomePage";

// Mock the entire axios package
vi.mock("axios");

describe("HomePage component", () => {
  let loadCart;

  beforeEach(() => {
    loadCart = vi.fn();

    // Mock the response of this http call
    axios.get.mockImplementation(async(urlPath) => {
      if (urlPath === "/api/products") {
        return {
          // Mock the format/template of a promis response
          data: [
            {
              id: "e43638ce-6aa0-4b85-b27f-e1d07eb678c6",
              image: "images/products/athletic-cotton-socks-6-pairs.jpg",
              name: "Black and Gray Athletic Cotton Socks - 6 Pairs",
              rating: {
                stars: 4.5,
                count: 87,
              },
              priceCents: 1090,
              keywords: ["socks", "sports", "apparel"],
            },
            {
              id: "15b6fc6f-327a-4ec4-896f-486349e85a3d",
              image: "images/products/intermediate-composite-basketball.jpg",
              name: "Intermediate Size Basketball",
              rating: {
                stars: 4,
                count: 127,
              },
              priceCents: 2095,
              keywords: ["sports", "basketballs"],
            }
          ],
        };
      }
    });
  });

  (it("displays the products correctly"),
    async() => {
      render(<HomePage cart={[]} localCart={loadCart} />);
      // "find" will wait the component to load, it is async and returns a promise.
      const productContainers = await screen.findAllByTestId("product-container");

      expect(productContainers.length).toBe(2);

      expect(within(productContainers[0])
      .getByText("Black and Gray Athletic Cotton Socks - 6 Pairs"))
      .toBeInTheDocument();

      expect(within(productContainers[1])
      .getByText("Intermediate Size Basketball"))
      .toBeInTheDocument();
    });
});
