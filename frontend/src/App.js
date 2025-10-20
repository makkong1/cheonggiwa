import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Main from './Routes/Main';
import User from './Routes/User';
import Booking from './Routes/Booking';
import Login from './Routes/Login';
import Signup from './Routes/Signup';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Main />} />
        <Route path="/user" element={<User />} />
        <Route path="/booking" element={<Booking />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
      </Routes>
    </Router>
  );
}

export default App;