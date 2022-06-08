import React from 'react';
import { MemoryRouter } from 'react-router';
import { render, renderWithoutRouter, screen } from '../test-utils';
import App from './App';

describe('App-Container with routing-functionality', () => {
  test('renders default page, login spinner and copyright', () => {
    render(<App />);

    expect(screen.getByText(/Songskipper for Spotify/i)).toBeInTheDocument();

    const currentPageLink = screen.getByRole('link', { current: 'page' });
    expect(currentPageLink).toContainHTML('Currently Playing');

    const main = screen.getByRole('main');
    const progress = screen.getByRole('progressbar');
    expect(main).toContainElement(progress);

    expect(screen.getByText(/Copyright/)).toBeInTheDocument();
  });

  test('renders skipRules page, when url is /rules', () => {
    renderWithoutRouter(
      <MemoryRouter initialEntries={["/rules"]}>
        <App />
      </MemoryRouter>
    );
    const currentPageLink = screen.getByRole('link', { current: 'page' });
    expect(currentPageLink).toContainHTML('Skip Rules');
  });


  test('renders playlog page, when url is /log', () => {
    renderWithoutRouter(
      <MemoryRouter initialEntries={["/log"]}>
        <App />
      </MemoryRouter>
    );
    const currentPageLink = screen.getByRole('link', { current: 'page' });
    expect(currentPageLink).toContainHTML('Playlog');
  });

  test('no active page, when url is /something-random/foo', () => {
    renderWithoutRouter(
      <MemoryRouter initialEntries={["/something-random/foo"]}>
        <App />
      </MemoryRouter>
    );
    const currentPageLink = screen.queryByRole('link', { current: 'page' });
    expect(currentPageLink).toBeNull();
  });
});