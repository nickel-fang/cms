import React from 'react';
import { HashRouter, Route } from 'react-router-dom';
import Layout from './layout';
import '../styles/common.less';


const App = () => {
  return (
    <HashRouter>
      <Layout />
    </HashRouter>
  );
};

export default App;
