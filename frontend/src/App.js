import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Main from './Routes/Main';
import User from './Routes/User';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Main />} />
        <Route path="/user" element={<User />} />
      </Routes>
    </Router>
  );
}

export default App;