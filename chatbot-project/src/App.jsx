import './App.css';
import { useState } from 'react';
import { ChatInput } from './components/ChatInput';
import { ChatMessage } from './components/ChatMessage';
import ChatMessages from './components/ChatMessages';


function App() {
    const [chatMessages, setChatMessages] = useState(
      [{
        message: 'hello chatbot',
        sender: 'user',
        id: 'id1'
      }, {
        message: 'Hello! How can I help you?',
        sender: 'robot',
        id: 'id2'
      }, {
        message: 'can you get todays day',
        sender: 'user',
        id: 'id3'
      }, {
        message: 'Today is July 22',
        sender: 'robot',
        id: 'id4'
      }]
    );

  // const [chatMessages, setChatMessages] = array;
  // const chatMessages = array[0];
  // const setChatMessages = array[1];
  return (
      <>
        <ChatInput 
          chatMessages={chatMessages}
          setChatMessages={setChatMessages}
        />
        <ChatMessages 
          chatMessages={chatMessages}
        />
    </>
  );

}

export default App
