import { ThemeProvider } from '@mui/material';
import { render, RenderOptions } from '@testing-library/react';
import React, { FC, ReactElement } from 'react';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import { store } from './app/store';
import { theme } from './app/theme';

const AllTheProviders: FC = ({ children }) => {
  return (
    <BrowserRouter>
      <Provider store={store}>
        <ThemeProvider theme={theme}>
          {children}
        </ThemeProvider>
      </Provider>
    </BrowserRouter>
  );
};


const customRender = (
  ui: ReactElement,
  options?: Omit<RenderOptions, 'wrapper'>,
) => render(ui, { wrapper: AllTheProviders, ...options });

// re-export everything
export * from '@testing-library/react';
// override render method
export { customRender as render };

