import { ThemeProvider } from '@mui/material';
import { render, RenderOptions } from '@testing-library/react';
import { FC, ReactElement } from 'react';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import { store } from './app/store';
import { theme } from './app/theme';

type Props = {
  children?: React.ReactNode;
};
const allTheProviders: FC<Props> = ({ children }) => (
  <BrowserRouter>
    <Provider store={store}>
      <ThemeProvider theme={theme}>
        {children}
      </ThemeProvider>
    </Provider>
  </BrowserRouter>
);
const allTheProvidersWithoutRouter: FC<Props> = ({ children }) => (
  <Provider store={store}>
    <ThemeProvider theme={theme}>
      {children}
    </ThemeProvider>
  </Provider>
);


const customRender = (
  ui: ReactElement,
  options?: Omit<RenderOptions, 'wrapper'>,
) => render(ui, { wrapper: allTheProviders, ...options });


const renderWithoutRouter = (
  ui: ReactElement,
  options?: Omit<RenderOptions, 'wrapper'>,
) => render(ui, { wrapper: allTheProvidersWithoutRouter, ...options });

// re-export everything
export * from '@testing-library/react';
// override render method
export { customRender as render };
export { renderWithoutRouter };

