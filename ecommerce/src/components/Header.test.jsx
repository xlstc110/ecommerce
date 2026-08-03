import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router";
import { describe, expect, it, vi } from "vitest";
import { Header } from "./Header";

describe("Header authentication controls", () => {
  it("shows the authenticated user and invokes logout", async () => {
    const onLogout = vi.fn();
    const user = userEvent.setup();

    render(
      <MemoryRouter>
        <Header cart={[]} username="testuser" onLogout={onLogout} />
      </MemoryRouter>,
    );

    expect(screen.getByText("Hello, testuser")).toBeInTheDocument();
    await user.click(screen.getByRole("button", { name: "Sign out" }));
    expect(onLogout).toHaveBeenCalledOnce();
  });
});
