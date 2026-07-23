import RobotImage from '../assets/robot.png';
import UserImage from '../assets/user.png';

export function ChatMessage({message, sender}) {
  //const {message, sender} = props;

  return (
    <div>
      {sender === 'robot' && <img src={RobotImage} width="50"/>}
      {message}
      {sender === 'user' && <img src={UserImage} width="50"/>}
    </div>
  );
}